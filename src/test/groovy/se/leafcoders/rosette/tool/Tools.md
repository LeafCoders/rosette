# Tools

Run these tools as unit test and point to a running Rosette server.

## Import educations and recordings as SimpleEducation

Educations shall be specified in a csv file with format:

```
educationTypeId,educationThemeId,time,authorName,authorUserId,title,recordingFileName

Example:
gudstjanst,56ccd06f30048e956ce6fda2,2016-02-27 11:00,Predikant Patrik,,Profeter i vår tid,audio_20160227.mp3
gudstjanst,56ccd06f30048e956ce6fda2,2016-03-03 11:00,,56ccd06d30048e956ce6fd69,Profeter på bibelns tid,audio_20160303.mp3
```

Run the tool with:

```
gradle test -Dtest.single=ImportEducations -Drosette.baseUrl=http://localhost:9000/ -Drosette.username=admin@admin.se -Drosette.password=password -Drosette.educationsCSV=educations.csv -Drosette.recordingsPath=./ -Drosette.uploadFolder=predikningar
```
