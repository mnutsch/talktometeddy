package com.talkingteddy;

import android.content.Context;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Matches response digests to task objects.
 */
public class TaskDiscriminator {
    private static HashMap<String, Task> taskStorage = new HashMap<String, Task>();
    private static Context context;

    public TaskDiscriminator(Context context) {
        this.context = context;
        loadTasksFromFile("tasks");
    }

    public static Task getTask(ResponseDigest responseDigest) {
        Task result = taskStorage.get(responseDigest.getMatchingPrompt());

        //TODO: use constant from the helper class
        if (responseDigest.getMatchingPromptScore() < 35 || result == null) {
            result = taskStorage.get("!!!FALLBACK!!!");
        }

        return result;
    }

    public static void register(String prompt, Task task) {
        taskStorage.put(prompt, task);
    }

    public List<String> enumeratedURL() {
        List<String> result = new ArrayList<String>();
        result.addAll(taskStorage.keySet());
        return result;
    }


    private void loadTasksFromFile(String tasks) {
        Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.tasks));
        scanner.useDelimiter("#");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner token = new Scanner(line);

            String question = token.next();
            List<String> answers = new LinkedList<String>();

            while (token.hasNext()) {
                answers.add(0, token.next());
            }

            String[] answersArray = answers.toArray(new String[answers.size()]);

            taskStorage.put(question, new BasicTask(question, answersArray));
        }

    }



}
