oc set resources dc spring-boot-demo --limits=memory=1Gi --requests=memory=1Gi
oc expose dc spring-boot-demo  --port=8080
oc expose svc spring-boot-demo
oc policy add-role-to-user edit system:serviceaccount:lexis-nexis-jenkins:jenkins
oc create -f src/main/resources/templates/buildconfig.yaml