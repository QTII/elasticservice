### sample-play-prod/

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

```
$ cd sample-play-prod
$ bin\elasticservice-sample.bat -Dconfig.resource=production.conf
```

To assign certain port and IP address. Default port is 9000.

```
$ bin\elasticservice-sample.bat -Dconfig.resource=production.conf -Dhttp.port=9090 -Dhttp.address=127.0.0.1
```

To test, connect with http://localhost:9090/assets/index.html

To shutdown, press Ctrl+C
