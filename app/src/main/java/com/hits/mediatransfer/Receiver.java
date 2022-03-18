package com.hits.mediatransfer;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by charleston on 15/09/17.
 */

public class Receiver extends BroadcastReceiver {

    public Receiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "receiver started", Toast.LENGTH_LONG).show();
        JobScheduler mJobScheduler = (JobScheduler)
                context.getSystemService( Context.JOB_SCHEDULER_SERVICE );
        JobInfo.Builder builder = new JobInfo.Builder( 1234,
                new ComponentName( context.getPackageName(),
                        JobSchedulerService.class.getName() ) );
        //builder.setPeriodic(5000);//every 3 seconds
        builder.setPersisted(true);//persist after reboot
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        builder.setRequiresCharging(true);//on charging the phone
        //builder.setRequiresDeviceIdle(true);//devide is not being used
        if( mJobScheduler.schedule( builder.build() ) <= 0 ) {
            //If something goes wrong
            Log.e("service", "deu merda");
        }

    }


}
