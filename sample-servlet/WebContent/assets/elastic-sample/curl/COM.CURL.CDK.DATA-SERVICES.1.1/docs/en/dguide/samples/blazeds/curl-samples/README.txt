                     ==============================
                     +                            +
                     +       CDK-DS Samples       +
                     +                            +
                     ==============================


INTRODUCTION
============
This is the Curl Data Kit - Data Services (CDK-DS) samples distribution.


PREREQUISITES
=============
It is assumed that you have the Curl 6.0 RTE and IDE installed and operational
on your machine. If you have not already done so, you can download and install
the Curl 6.0 RTE and IDE from the Curl website:

http://www.curl.com/download/all_downloads.php

The CDK-DS samples come ready for use with the BlazeDS Turnkey distribution
3.2.0.3978, and assumes you have BlazeDS Turnkey 3.2.0978 running on your
system. If you have not already done so, please setup BlazeDS Turnkey. Refer to
the BlazeDS documentation as necessary.


INSTALLATION
============
This README, as well as some other CDK-DS documents, refers to the BlazeDS
Turnkey installation location as [installdir]. The CDK-DS samples can be
unzipped into the

[installdir]/tomcat/webapps/

directory and will create a directory structure underneath a directory named
blazeds-curl-samples.


CONFIGURATION
=============
BlazeDS uses an Apache Tomcat server/servlet container. In order to run the
CDK-DS sample applets, Tomcat needs to be configured to serve Curl files. What
follows here is a minimal description to get the samples up and running. If you
have the Curl IDE installed you can refer to the "Configuring Your Web Server"
section of the Curl Developer's Guide for more information regarding server
configuration.

  * Configure the following MIME type mappings in Tomcat's main configuration
    web.xml file, located at [installdir]/tomcat/conf/web.xml

        <mime-mapping>
          <extension>curl</extension>
          <mime-type>text/vnd.curl</mime-type>
        </mime-mapping>
        <mime-mapping>
          <extension>dcurl</extension>
          <mime-type>text/vnd.curl.dcurl</mime-type>
        </mime-mapping>

  * Deploy a Pro curl-license-5.dat file to either the

        [installdir]/tomcat/webapps/ROOT

    or

        [installdir]/tomcat/webapps/blazeds-curl-samples

    directory. 

    A curl-license-5.dat file for use with http://localhost urls can
    be found in your Curl installation. For example, if you installed the Curl
    IDE on Windows in the location C:\Program Files\Curl Corporation\ then you
    can find a license file in the

    C:\Program Files\Curl Corporation\Surge\7\ide\etc\localhost

    directory, and a Pro licence file in the

    C:\Program Files\Curl Corporation\Surge\7\ide\etc\localhost-pro

    directory.

  * Create and deploy a curl-access.txt file in the

        [installdir]/tomcat/webapps/ROOT

    directory. Its contents should look like this:

        version: 2.0
        allow: localhost


RUNNING THE SAMPLES
===================
After Tomcat has been configured, launch Tomcat and the BlazeDS sampledb as
instructed by the BlazeDS documentation. The default setup for BlazeDS uses port
8400, so you can start the CDK-DS samples by opening a browser and going to the
following url:

http://localhost:8400/blazeds-curl-samples/start.curl

The start file repeats some of these instructions, and provides some additional
information. It also directs you to the Curl Test Drive page, which contains
links to, and descriptions of, each of the CDK-DS samples.


NOTES
=====
* All samples that access data through a local server url use localhost as the
  hostname, and 8400 as the port.
