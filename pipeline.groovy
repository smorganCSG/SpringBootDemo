#!groovy

node('maven') {

	def ocdevnamespace = "lexis-conference-services"
	
	def ocprodnamespace = "srm-parks-prod"
	def appname = "spring-boot-demo";

	def mvnCmd = "mvn "

	stage('Checkout Source') {
		echo "Checking out source"
		checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [
				[credentialsId: 'smorgancsg', url: 'https://smorgan@gitlab.cardinalsolutions.com/smorgan/ConferenceSpeakers.git']
			]])
	}

	def version    = getVersionFromPom("pom.xml")
	def newTag = "TestReady-${version}:${BUILD_NUMBER}"



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

		openshiftDeploy depCfg: appname, namespace: ocdevnamespace, verbose: 'false', waitTime: '', waitUnit: 'sec'
		openshiftVerifyDeployment depCfg: appname, namespace: ocdevnamespace, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
	}

}

def getVersionFromPom(pom) {
	def matcher = readFile(pom) =~ '<version>(.+)</version>'
	matcher ? matcher[0][1] : null
}
