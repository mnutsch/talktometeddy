#!/usr/bin/env python
import os
from datetime import datetime
from flask import Flask, abort, request, jsonify, g, url_for
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.httpauth import HTTPBasicAuth
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired

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
def verify_password(username_or_token, password):
    # first try to authenticate by token
    user = User.verify_auth_token(username_or_token)
    if not user:
        # try to authenticate with username/password
        user = User.query.filter_by(username = username_or_token).first()
        if not user or not user.verify_password(password):
            return False
    g.user = user
    return True

@app.route('/api/users', methods = ['POST'])
def new_user():
    username = request.json.get('username')
    email = request.json.get('email')
    password = request.json.get('password')
    if username is None or password is None:
        abort(400) # missing arguments
    if User.query.filter_by(username = username).first() is not None:
        abort(400) # existing user
    user = User(username, email)
    user.hash_password(password)
    db.session.add(user)
    db.session.commit()
    return jsonify({ 'username': user.username }), 201, {'Location': url_for('get_user', id = user.id, _external = True)}

@app.route('/api/users/<int:id>')
def get_user(id):
    user = User.query.get(id)
    if not user:
        abort(400)
    return jsonify({ 'username': user.username })

@app.route('/api/token')
@auth.login_required
def get_auth_token():
    token = g.user.generate_auth_token(604800)
    return jsonify({ 'token': token.decode('ascii') })

@app.route('/api/resources/<int:id>')
def get_resource(id):
    activity = Activity.query.get(id)
    if not activity:
        abort(400)
    return jsonify({'data': activity.serialize})

@app.route('/api/resources')
@auth.login_required
def get_resources():
    activities = Activity.query.filter_by(user_id = g.user.id)
    return jsonify({ 'data': [i.serialize for i in activities.all()]})

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