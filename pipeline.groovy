#!groovy

node('maven') {

  def ocdevnamespace = "srm-conference-services"
  def ocprodnamespace = "srm-parks-prod"
  def appname = "speakers";

  def mvnCmd = "mvn "

  stage('Checkout Source') {
    echo "Checking out source"
    checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'smorgancsg', url: 'https://smorgan@gitlab.cardinalsolutions.com/smorgan/ConferenceSpeakers.git']]])
}
  
  def version    = getVersionFromPom("pom.xml")
  def newTag = "TestReady-${version}:${BUILD_NUMBER}"
 
  stage('Flyway Migration') {
  	flywayrunner commandLineArgs: '', credentialsId: 'SampleDB', flywayCommand: 'baseline', installationName: 'flyway', locations: 'classpath:db/migrations', url: 'jdbc:mysql://mysql.srm-conference-services.svc:3306/sampledb'
  }
  
  stage('Build war') {
    echo "Building version ${version}"
    sh "${mvnCmd} clean package -DskipTests"
  }

//   stage ('Testing & Coverage') {
//     parallel(
//       "Unit Tests": {
//         echo "Running Unit Tests"
//         sh "${mvnCmd} test"\
//       },
//       "Sonarqube":{
//         echo "Running Code Analysis"
//         sh "${mvnCmd} sonar:sonar -Dsonar.host.url=http://sonarqube.srm-sonarqube.svc.cluster.local:9000/ -Dsonar.projectName=${JOB_BASE_NAME}"
//       }
//     )
//   }
  
//   stage('Publish to Nexus') {
//     echo "Publishing to Nexus"
//     sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus3.srm-nexus.svc.cluster.local:8081/repository/releases"
//   }  
  
   stage('Build OpenShift Image') {
     echo "New Tag: ${newTag}"
     sh "oc project ${ocdevnamespace}"
     sh "oc start-build ${appname} --follow --from-file=./target/ConferenceServices-${version}.jar -n ${ocdevnamespace}"
     openshiftTag alias: 'false', destStream: appname, destTag: newTag, destinationNamespace: ocdevnamespace, namespace: ocdevnamespace, srcStream: appname, srcTag: 'latest', verbose: 'false'
   }   

   stage('Deploy to Dev') {
      sh "oc project ${ocdevnamespace}"
      sh "oc patch dc ${appname} --patch '{\"spec\": { \"triggers\": [ { \"type\": \"ImageChange\", \"imageChangeParams\": { \"containerNames\": [ \"${appname}\" ], \"from\": { \"kind\": \"ImageStreamTag\", \"namespace\": \"${ocdevnamespace}\", \"name\": \"$appname:$newTag\"}}}]}}' -n ${ocdevnamespace}"

      openshiftDeploy depCfg: appname, namespace: ocdevnamespace, verbose: 'true', waitTime: '', waitUnit: 'sec'
      openshiftVerifyDeployment depCfg: appname, namespace: ocdevnamespace, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
      openshiftVerifyService namespace: ocdevnamespace, svcName: appname, verbose: 'false'
    }    

//   newTag = "ProdReady-${version}:${BUILD_NUMBER}"
//   stage('Integration Test') {
//     echo "New Tag: ${newTag}"
//     openshiftTag alias: 'false', destStream: appname, destTag: newTag, destinationNamespace: ocdevnamespace, namespace: ocdevnamespace, srcStream: appname, srcTag: 'latest', verbose: 'false'
//   }

//   // Blue/Green Deployment into Production
//   // -------------------------------------
//   def dest   = "${appname}-green"
//   def active = ""

//   stage('Prepare Blue/Green Switch') {
//     sh "oc project ${ocprodnamespace}"
//     sh "oc get route ${appname} -n ${ocprodnamespace} -o jsonpath='{ .spec.to.name }' > activesvc.txt"
//     active = readFile('activesvc.txt').trim()
//     if (active == "${appname}-green") {
//       dest = "${appname}-blue"
//     }
//     echo "Active svc: " + active
//     echo "Dest svc:   " + dest
//   }

//  stage('Deploy To Prod') {
//     echo "Deploying to ${dest}"
//     sh "oc patch dc ${dest} --patch '{\"spec\": { \"triggers\": [ { \"type\": \"ImageChange\", \"imageChangeParams\": { \"containerNames\": [ \"$dest\" ], \"from\": { \"kind\": \"ImageStreamTag\", \"namespace\": \"$ocdevnamespace\", \"name\": \"$appname:$newTag\"}}}]}}' -n $ocprodnamespace"
//     openshiftDeploy depCfg: dest, namespace: ocprodnamespace, verbose: 'false', waitTime: '', waitUnit: 'sec'
//     openshiftVerifyDeployment depCfg: dest, namespace: ocprodnamespace, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'true', waitTime: '', waitUnit: 'sec'
//     openshiftVerifyService namespace: ocprodnamespace, svcName: dest, verbose: 'false'
//   }

//   stage('Switch over to new Version') {
//     input "Switch NationaParks to Production?"

//     sh "oc patch route ${appname} -n ${ocprodnamespace} -p '{\"spec\":{\"to\":{\"name\":\"" + dest + "\"}}}'"
    
//     sh "oc get route ${appname} -n ${ocprodnamespace} > oc_out.txt"
//     oc_out = readFile('oc_out.txt')
//     echo "Current route configuration: " + oc_out
//   }



}

def getVersionFromPom(pom) {
  def matcher = readFile(pom) =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}