#!/usr/bin/env python
import os
from datetime import datetime
from flask import Flask, abort, request, jsonify, g, url_for
from flask.ext.sqlalchemy import SQLAlchemy
from sqlalchemy import exc
from flask.ext.httpauth import HTTPBasicAuth
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired
from email.utils import parseaddr

# initialization
app = Flask(__name__)
app.config['SECRET_KEY'] = 'the quick brown fox jumps over the lazy dog guru nandesh shaili gappu'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://be9051b4ed9ac1:cd2c1a84@us-cdbr-east-05.cleardb.net/heroku_a2ad4526684b7e4'
app.config['SQLALCHEMY_COMMIT_ON_TEARDOWN'] = True

# extensions
db = SQLAlchemy(app)
auth = HTTPBasicAuth()

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key = True)
    username = db.Column('username', db.String(32), index = True)
    email = db.Column('email', db.String(45), index = True)
    password_hash = db.Column('password_hash', db.String(120))
    created_time = db.Column('created_time' , db.DateTime)

    def __init__(self , username, email):
        self.username = username
        self.email = email
        self.created_time = datetime.utcnow()

    def hash_password(self, password):
        self.password_hash = pwd_context.encrypt(password)

    def verify_password(self, password):
        return pwd_context.verify(password, self.password_hash)

    def generate_auth_token(self, expiration = 600):
        s = Serializer(app.config['SECRET_KEY'], expires_in = expiration)
        return s.dumps({ 'id': self.id })

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except SignatureExpired:
            return None # valid token, but expired
        except BadSignature:
            return None # invalid token
        user = User.query.get(data['id'])
        return user

class Activity(db.Model):
    __tablename__ = 'activities'
    id = db.Column(db.Integer, primary_key = True)
    reco = db.Column('reco', db.String(140))
    response = db.Column('response', db.String(140))
    user_id = db.Column('user_id', db.Integer)
    created_time = db.Column('created_time' , db.DateTime)

    def __init__(self, reco, response, user_id):
        self.reco = reco
        self.response = response
        self.user_id = user_id
        self.created_time = datetime.utcnow()

    @property
    def serialize(self):
       """Return Activity data in easily serializeable format"""
       return {
           'reco': self.reco,
           'response': self.response,
           'created_at' : self.created_time
       }

@auth.verify_password
def verify_password(email_or_token, password):
    # first try to authenticate by token
    user = User.verify_auth_token(username_or_token)
    if not user:
        # try to authenticate with username/password
        user = User.query.filter_by(email = email_or_token).first()
        if not user or not user.verify_password(password):
            return False
    g.user = user
    return True

def user_input_valid(username, email, password):
    if username is None or not username or len(username) < 1 or username.isspace():
        return False
    elif password is None or not password or len(password) < 1 or password.isspace():
        return False
    elif email is None or not email or len(email) < 1 or email.isspace():
        return False
    else:
        return True

#Register New User
@app.route('/api/users', methods = ['POST'])
def new_user():
    tag = request.json.get('tag')
    email = request.json.get('email')
    password = request.json.get('password')
    if tag == 'register':
        username = request.json.get('username')
        new_user_response = {}
        new_user_response["tag"] = "register"
        if not user_input_valid(username, email, password):
            new_user_response["success"] = 0
            new_user_response["error"] = 1
            new_user_response["error_msg"] = "Error occured in registartion, missing or invalid fullname, email or password"
            return jsonify(new_user_response) # missing arguments

        try:
            if User.query.filter_by(email = email).first() is not None:
                new_user_response["success"] = 0
                new_user_response["error"] = 1
                new_user_response["error_msg"] = "User already exist"
                return jsonify(new_user_response) # User already existed

            user = User(username, email)
            user.hash_password(password)
            db.session.add(user)
            db.session.commit()

            new_user_info = {}
            new_user_info["name"] = user.username
            new_user_info["email"] = user.email
            new_user_info["created_time"] = user.created_time

            new_user_response["success"] = 1
            new_user_response["error"] = 0
            new_user_response["uid"] = user.id
            new_user_response["user"] = new_user_info
            return jsonify(new_user_response)

        except exc.SQLAlchemyError:
            new_user_response["success"] = 0
            new_user_response["error"] = 1
            new_user_response["error_msg"] = "Error occured in registartion, try again"
            return jsonify(new_user_response) # connection to db failed
    else:
        login_user_response = {}
        login_user_response["tag"] = "login"
        login_user_response["success"] = 0
        login_user_response["error"] = 1

        try:
            user = User.query.filter_by(email = email).first()
            if not user or not user.verify_password(password):
                login_user_response["error_msg"] = "Incorrect email or password"
                return jsonify(login_user_response)

            login_user_info = {}
            login_user_response["success"] = 1
            login_user_response["error"] = 0
            login_user_info["name"] = user.username
            login_user_info["email"] = user.email
            login_user_info["created_time"] = user.created_time
            login_user_response["uid"] = user.id
            login_user_response["user"] = login_user_info

            return jsonify(login_user_response)

        except exc.SQLAlchemyError:
            login_user_response["success"] = 0
            login_user_response["error"] = 1
            login_user_response["error_msg"] = "Error occured in login, try again"
            return jsonify(login_user_response) # connection to db failed

#Login Existing User
@app.route('/api/users/<int:id>')
@auth.login_required
def get_user(id):
    login_user_response = {}
    login_user_response["tag"] = "login"
    login_user_response["success"] = 0
    login_user_response["error"] = 1

    user = User.query.get(id)
    if not user:
        login_user_response["error_msg"] = "User not registered"
        return jsonify(login_user_response)

    login_user_info = {}
    login_user_response["success"] = 1
    login_user_response["error"] = 0
    login_user_info["name"] = user.username
    login_user_info["email"] = user.email
    login_user_info["created_time"] = user.created_time
    login_user_response["uid"] = user.id
    login_user_response["user"] = login_user_info

    return jsonify(login_user_response)

#Get token
@app.route('/api/token')
@auth.login_required
def get_auth_token():
    token = g.user.generate_auth_token(604800)
    return jsonify({ 'token': token.decode('ascii') })

#Get user activity by id
@app.route('/api/resources/<int:id>')
def get_resource(id):
    activity = Activity.query.get(id)
    if not activity:
        abort(400)
    return jsonify({'data': activity.serialize})

#Post user activity
@app.route('/api/activity', methods = ['POST'])
def new_user_activity():
    reco = request.json.get('reco')
    response = request.json.get('response')
    uid = request.json.get('uid')
    new_activity_response = {}
    if reco is None or response is None or uid is None:
        new_activity_response["error"] = 1
        new_activity_response["success"] = 0
        new_activity_response["error_msg"] = "Reco or response is empty or invalid"
        return jsonify(new_activity_response)
    else:
        activity = Activity(reco, response, uid)
        db.session.add(activity)
        db.session.commit()
        new_activity_response["success"] = 1
        return jsonify(new_activity_response)

#Get all user's activities
@app.route('/api/resources')
@auth.login_required
def get_resources():
    activities = Activity.query.filter_by(user_id = g.user.id)
    return jsonify({ 'data': [i.serialize for i in activities.all()]})

#Post new user activity
@app.route('/api/resources', methods = ['POST'])
@auth.login_required
def new_resource():
    reco = request.json.get('reco')
    response = request.json.get('response')
    if reco is None or response is None:
        abort(400) # missing arguments
    user_id = g.user.id
    activity = Activity(reco, response, user_id)
    db.session.add(activity)
    db.session.commit()
    return jsonify({'response':201})

if __name__ == '__main__':
    app.run(debug = True)