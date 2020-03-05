package com.firebasejobdispatcher;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String JOB_TAG = "MyJobService";
    private FirebaseJobDispatcher mDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_schedule).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_schedule:
                scheduleJob();
                break;
            case R.id.btn_cancel:
                cancelJob(JOB_TAG);
                break;
        }
    }

    private void scheduleJob() {
        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class)  // Select the service you want to run.
                .setTag(JOB_TAG)  // Set a unique id for job
                .setRecurring(true) // Repeat
//                .setTrigger(Trigger.executionWindow(60, 120))   // Run between 60 - 120 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 30)) // Start from 0 to 30 seconds (paired with setRecurring)
//                .setLifetime(Lifetime.FOREVER)   //persist the task across boots
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT) // This job will remain until the system is rebooted
                .setReplaceCurrent(false) // Do not overwrite job with the same tag name
                .setConstraints(Constraint.ON_ANY_NETWORK) // will work when connected to a network
//                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL) // When a Retry matric fail
                .build();
        mDispatcher.mustSchedule(myJob);
        Toast.makeText(this, R.string.job_scheduled, Toast.LENGTH_LONG).show();
    }

    private void cancelJob(String jobTag) {
        if ("".equals(jobTag)) {
            mDispatcher.cancelAll();
        } else {
            mDispatcher.cancel(jobTag);
        }
        Toast.makeText(this, R.string.job_cancelled, Toast.LENGTH_LONG).show();
    }
}