node {
    def MvnHome=tool name: 'maven-3', type: 'maven'
	def MavenCMD="${MvnHome}/bin/mvn"
	def docker=tool name: 'docker', type: 'dockerTool'
	def DockerCMD="${docker}/bin/docker"
	try {
        stage('GitHub Repo checkout & Preparation') { 
            git credentialsId: 'GitHub', url: 'https://github.com/GautamDJain/WebApp_BootCamp.git'
        }
        stage('Maven Build, Unit test & Package') {
		sh 'java -version'
            sh "${MavenCMD} clean package"       
        }
		stage('deploy on tomcat')
        {
            //deploy adapters: [tomcat8(credentialsId: 'tomcatadmin', path: '', url: 'http://localhost:8090/')], contextPath: 'webapp', war: '**/*.war'
        }
        stage('Docker image build & push to Docker Hub') {
            sh "${DockerCMD} --version"
		    //sh "service ${DockerCMD} start"
            sh "${DockerCMD} build -t gautamjainsagar/myjavawebappimage ."
            withCredentials([string(credentialsId: 'DockerHubPass', variable: 'dockerHubPass')]) {
               sh "${DockerCMD} login -u gautamjainsagar -p ${dockerHubPass}"     
            }
	        sh "${DockerCMD} push gautamjainsagar/myjavawebappimage"
        }
	stage('Launch new EC2 instance with ansible'){
        ansiblePlaybook becomeUser: 'ec2-user', credentialsId: 'Docker_AWSUser_SSH', installation: 'ansible', playbook: 'Create-ec2-playbook.yml', sudoUser: 'ec2-user'
    }
        def prodIp = "NULL"
		def instanceId = "NULL"
        stage('Get Prod server IP address'){
		def instanceFilter = 'aws ec2 describe-instances --filters "Name=tag-value,Values=Production" --query "Reservations[*].Instances[*].InstanceId[]" --output text'
            def ipFilter = 'aws ec2 describe-instances --filters "Name=tag-value,Values=Production" --query "Reservations[*].Instances[*].PublicIpAddress[]" --output text'
            instanceId = sh (script: "${instanceFilter}", returnStdout:true).trim()
            println instanceId
	    prodIp = sh (script: "${ipFilter}", returnStdout:true).trim()
            println prodIp
		
        }
        stage('Install & Run Docker on AWS new instance'){
            def installCMD = 'sudo yum install docker -y'
		    def startCMD = 'sudo service docker start'
            sshagent(['Docker_AWSUser_SSH']) {
              sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} ${installCMD}"
			  sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} sleep 5"
		      sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} ${startCMD}"
            }
        }
        stage('Docker image pull & run container') {
	   def stopCMD = 'sudo docker stop javawebapp || true'
           def removeCMD = 'sudo docker rm -f javawebapp || true'
           def runCMD = 'sudo docker run -d -p 8082:8080 --name javawebapp gautamjainsagar/myjavawebappimage '
           sshagent(['Docker_AWSUser_SSH']) {
	      sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} ${stopCMD}"
	      sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} ${removeCMD}"
              sh "ssh -o StrictHostKeyChecking=no ec2-user@${prodIp} ${runCMD}"
            }
        }
    }
    catch(e){
        currentBuild.result="FAILURE"
        stage('Email Notificatin') {
           mail bcc: '', body: "Your Jenkins Job ${env.JOB_NAME} build is ${currentBuild.currentResult} for build number ${env.BUILD_NUMBER}. \nYou can check Jenkin job console output info at: ${env.BUILD_URL}", cc: '', from: '', replyTo: '', subject: "Jenkins Build Job ${env.JOB_NAME} status is ${currentBuild.currentResult}", to: 'gautamjain2011@gmail.com'
        }
    }
}
