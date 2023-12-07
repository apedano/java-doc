https://ibm.github.io/workshop-quarkus-openshift-reactive-messaging/


https://github.com/luszczynski/amq-streams-demo/blob/master/README.md

https://developers.redhat.com/blog/2020/06/15/tracking-covid-19-using-quarkus-amq-streams-and-camel-k-on-openshift#set_up_and_run_the_first_application

The server is accessible via web console at:
  https://console-openshift-console.apps-crc.testing

Log in as administrator:
  Username: kubeadmin
  Password: A4edN-TesvE-JeJMs-j678U

Log in as user:
  Username: developer
  Password: developer

Use the 'oc' command line interface:

> @FOR /f "tokens=*" %i IN ('crc oc-env') DO @call %i
> oc login -u developer https://api.crc.testing:6443



https://access.redhat.com/documentation/en-us/red_hat_amq_streams/2.1/html-single/getting_started_with_amq_streams_on_openshift/index


Installer AMQ streams

Install the kafka cluster (1 node), fix the warnings in the configuraiton
