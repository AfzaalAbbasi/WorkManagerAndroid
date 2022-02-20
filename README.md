# WorkManagerAndroid
Blur Image Using Work Manager Android  

In this Example we have image and we are applying Blur Levels on image with Workmanager

To include WorkManager in your project, add the following dependencies to your app's build.gradle file:

    implementation "androidx.activity:activity-ktx:1.4.0"
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "androidx.work:work-runtime-ktx:2.7.1"
    
# Worker: 
This is where you put the code for the actual work you want to perform in the background. You'll extend this class and override the doWork() method.
# WorkRequest:
This represents a request to do some work. You'll pass in your Worker as part of creating your WorkRequest. When making the WorkRequest you can also specify things like Constraints on when the Worker should run.
# WorkManager: 
This class actually schedules your WorkRequest and makes it run. It schedules WorkRequests in a way that spreads out the load on system resources, while honoring the constraints you specify.


# Get WorkManager in the ViewModel

create a viewModel Class that extends androidx.lifecycle.ViewModel.

```
private val workManager = WorkManager.getInstance(application)

```
Use LiveData in Activity to observe all the published progress
```
 init {
        // This  makes sure that whenever the current work Id changes the WorkInfo the UI is listening to changes
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        imageUri = getImageUri(application.applicationContext)
    }
```

# Define the observer function

Define observer function in Activity that listens to progress of Work Manager.

```
viewModel.outputWorkInfos.observe(this, workInfosObserver())

private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = listOfWorkInfo[0]
            if (workInfo.state.isFinished) {
                showWorkFinished()          
            }
            else {
                showWorkInProgress()
            }
        }
    }
    
```


