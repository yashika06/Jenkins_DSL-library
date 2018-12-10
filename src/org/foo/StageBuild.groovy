package org.foo

class StageBuild implements Serializable{
    
	def jobname=""
    
    def script
	
    StageBuild(script) {
        println "initialization StageBuild"
        this.script = script
		
    }
    
    def scmStageExecution(){
         this.script.build job: this.getScmJobName()
    }
    
    def buildStageExecution(sonarqube){
	
        this.script.build job: this.getBuildJobName(), wait: true, parameters: [[$class: 'BooleanParameterValue', name: 'SonarQube', value: Boolean.valueOf(sonarqube)]]
		
		this.script.emailext (
		  mimeType: 'text/html',
		  subject: "Await for Approval: Job '${this.script.env.JOB_NAME} [${this.script.env.BUILD_NUMBER}]'",
		  body: """<p>Await for Approval: Job '${this.script.env.JOB_NAME} [${this.script.env.BUILD_NUMBER}]':</p>
			<p>Check console output at "<a href="${this.script.env.BUILD_URL}console">${this.script.env.JOB_NAME} [${this.script.env.BUILD_NUMBER}]</a>"</p>""",
		  to: 'yashika.singhal@cummins.com'
		)
		this.script.timeout(time:30, unit:'MINUTES') {
			this.script.input message:'Approve Build?', submitter: 'admin'
		}
    }
	def qualityGateExecution(){
		
		def qg = this.script.waitForQualityGate()
		this.script.println "Status: ${qg.status}"
		if (qg.status != 'OK') {
		  this.script.error "Pipeline aborted due to quality gate failure: ${qg.status}"
		}
        
	}
	
	def deployStageExecution(){
		this.script.build job: this.getDeployJobName(), wait: true
	}
	
	def emailStageExecution(build_status){
		
		this.script.build job: this.getEmailNotificationJobName(), parameters: [[$class: 'StringParameterValue', name: 'jobName', value: "${this.script.env.JOB_NAME}"],[$class: 'StringParameterValue', name: 'jobStatus', value: build_status],[$class: 'StringParameterValue', name: 'buildURL', value: "${this.script.env.BUILD_URL}console"],[$class: 'StringParameterValue', name: 'buildNumber', value: "${this.script.env.BUILD_NUMBER}"]]
	}
	
	def getScmJobName(){
		jobname="SCM-checkout"
		return jobname
	}
	
	def getBuildJobName(){
		jobname="Build"
		return jobname
	}
	def getDeployJobName(){
		jobname="Deploy"
		return jobname
	}
	def getEmailNotificationJobName(){
		jobname="Email-Notification"
		return jobname
	}

}
