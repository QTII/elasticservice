## Elastic Service - SQL Query Repository Framework

Elastic Service is coded in Scala, Play Framework. Developers save SQL statements in the form of XML file in a certain directory. Once it starts SQLs in that directory loaded into the cache. On request through HTTP, Elastic Service maps given parameter's values by client into the corresponding SQL statement and then executes it on the database.

### Folders

- framework/ - Elastic Service source project
- sample-play-dev/ - Sample source project in Play
- sample-play-prod/ - Sample binary in Play on production mode
- sample-servlet/ - Sample servlet project
- esConfig/ - Configuration file and SQL XML files

### framework/

```
$ cd framework
$ sbt
> compile   -> generates target\scala-2.11\classes
> package   -> generates target\scala-2.11\elasticservice_2.11-0.1.1.jar
```

### sample-play-dev/

In order to test this sample application, database should be initialized first.
H2 databse is used here therefore if you do not have H2 in your computer, please download H2 from http://www.h2database.com/html/cheatSheet.html. You can download jar file(such as h2-1.4.190.jar) from there then save it wherever you want and then in command line, move to that folder and run following command: 
```
  $ java -jar h2-1.4.190.jar
```

You will be connected with H2 console automatically in browser. On the console create database file. There are several ways to create it. Here we will explain the way in which the file is created in the OS user's home directory like following:
   - Fill in the JDBC URL 'jdbc:h2:~/test'
   - Press 'Connect' button
   - Check out test.mv.db file created in your home directory

Configure in conf/application.conf file like following:
```
      db.default.driver=org.h2.Driver
      db.default.url="jdbc:h2:~/test"
      #db.default.url="jdbc:h2:tcp://localhost/~/test"
      db.default.username= sa
      db.default.password=""
```

Then you have done all you have to do for usig database.	  

Copy framework\generates target\scala-2.11\elasticservice_2.11-0.1.1.jar to sample-play-dev\lib

```
$ cd sample-play-dev
$ activator
[sample-play-dev] $ compile
[sample-play-dev] $ ~ run
```

Then you are ready to test this sample application.
Connect with http://localhost:9000/assets/index.html

To shutdown, press Ctrl+D

### Making stage mode files

```
$ cd sample-play-dev
$ activator
```

To generate secret

```
[sample-play-dev] $ playGenerateSecret
[info] Generated new secret: QCYtAnfkaZiwrNwnxIlR6CTfG3gf90Latabg5241ABR5W1uDFNIkn
```

Copy the secret above and paste in conf/production.conf as the value of application.secret
For example:
```
application.secret="QCYtAnfkaZiwrNwnxIlR6CTfG3gf90Latabg5241ABR5W1uDFNIkn"
```

```
$ activator clean stage
```

Command above generates target/universal/stage directory.

### sample-play-prod/

Copy all files in target/universal/stage directory to in ..\sample-play-prod

```
$ cd sample-play-prod
$ bin\sample.bat -Dconfig.resource=production.conf
```

To assign certain port and IP address. Default port is 9000.

```
$ bin\sample.bat -Dconfig.resource=production.conf -Dhttp.port=9090 -Dhttp.address=127.0.0.1
```

To test, connect with http://localhost:9090/assets/index.html

To shutdown, press Ctrl+C

### sample-servlet/

Copy all files in sample-play-dev\public into sample-servlet\WebContent\assets.

Copy sample-play-dev\target\scala-2.11\classes into sample-servlet\WebContent\WEB-INF.

Copy all files in sample-play-prod\lib into sample-servlet\WebContent\WEB-INF\lib.

Delete sample.sample-0.1-SNAPSHOT-assets.jar, sample.sample-0.1-SNAPSHOT-sans-externalized.jar files from sample-servlet\WebContent\WEB-INF\lib.

Deploy sample-servlet\WebContent directory in any Web Application Server.

### License

This software is licensed under the Apache 2 license, quoted below.

Copyright (C) 2009-2016 QTI International Inc. (https://www.qtii.co.kr).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
