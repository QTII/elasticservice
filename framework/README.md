## Elastic Service - SQL Query Repository Framework

Elastic Service is coded in Scala, Play Framework. Developers save SQL statements in the form of XML file in a certain directory. Once it starts SQLs in that directory loaded into the cache. On request through HTTP, Elastic Service maps given parameter's values by client into the corresponding SQL statement and then executes it on the database.

### Folders

- elasticservice - Elastic Service source
- elasticservice-sample-play-dev - Sample source in Play
- elasticservice-sample-play-prod - Sample binary in Play on production mode
- elasticservice-sample-servlet - Sample servlet
- esConfig - Configuration file and SQL XML files

### elasticservice

```
$ cd elasticservice
$ sbt
> compile   -> generates target\scala-2.11\classes
> package   -> generates target\scala-2.11\elasticservice_2.11-0.1.1.jar
```

### elasticservice-sample-play-dev

Copy elasticservice\generates target\scala-2.11\elasticservice_2.11-0.1.1.jar to elasticservice-sample-play-dev\lib

```
$ cd elasticservice-sample-play-dev
$ activator
[elasticservice-sample-play-dev] $ compile
[elasticservice-sample-play-dev] $ ~ run
```

To test, connect with http://localhost:9000/assets/index.html

To shutdown, press Ctrl+D

### Making stage mode files

```
$ cd elasticservice-sample-play-dev
$ activator
```

To generate secret

```
[elasticservice-sample-play-dev] $ playGenerateSecret
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

### elasticservice-sample-play-prod

Copy all files in target/universal/stage directory to in ..\elasticservice-sample-play-prod

```
$ cd elasticservice-sample-play-prod
$ bin\elasticservice-sample.bat -Dconfig.resource=production.conf
```

To assign certain port and IP address. Default port is 9000.

```
$ bin\elasticservice-sample.bat -Dconfig.resource=production.conf -Dhttp.port=9090 -Dhttp.address=127.0.0.1
```

To test, connect with http://localhost:9090/assets/index.html

To shutdown, press Ctrl+C

### elasticservice-sample-servlet

Copy all files in elasticservice-sample-play-dev\public into elasticservice-sample-servlet\WebContent\assets.

Copy elasticservice-sample-play-dev\target\scala-2.11\classes into elasticservice-sample-servlet\WebContent\WEB-INF.

Copy all files in elasticservice-sample-play-prod\lib into elasticservice-sample-servlet\WebContent\WEB-INF\lib.

Delete elasticservice-sample.elasticservice-sample-0.1-SNAPSHOT-assets.jar, elasticservice-sample.elasticservice-sample-0.1-SNAPSHOT-sans-externalized.jar files from elasticservice-sample-servlet\WebContent\WEB-INF\lib.

Deploy elasticservice-sample-servlet\WebContent directory in any Web Application Server.

### License

This software is licensed under the Apache 2 license, quoted below.

Copyright (C) 2009-2016 QTI International Inc. (https://www.qtii.co.kr).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.