package org.foo

class DevBuild implements Serializable{
    
	def jobname=""
    
    def script
	
    DevBuild(script) {
        println "initialization DevBuild"
        this.script = script
		
    }
    
    def scmStageExecution(){
         this.script.build job: this.getScmJobName()
    }
    
    def buildStageExecution(sonarqube){
        this.script.build job: this.getBuildJobName(), parameters: [[$class: 'BooleanParameterValue', name: 'SonarQube', value: Boolean.valueOf(sonarqube)]]
    }
	
	def deployStageExecution(){
		this.script.build job: this.getDeployJobName()
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
