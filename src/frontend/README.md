Links for pushing to ECR:

https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config-creds.html

https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-where

https://docs.aws.amazon.com/powershell/latest/userguide/specifying-your-aws-credentials.html

https://docs.docker.com/engine/context/ecs-integration/#run-compose-applications


To get app on EC2:

* Create EC2 instance
  * Don't forget to setup HTTP and HTTPS in security and set Source of SSH to My IP
  * Don't forget to up disk space to 8 Gigs (could go up to 30, we'll see). Need this to use for swap so we can build docker image.
* Assign elastic IP
* Change NameCheap DNS
* SSH in
  * cd .ssh
  * ssh -i .\aws_ec2.pem ec2-user@ec2-3-131-78-151.us-east-2.compute.amazonaws.com
    * (last part changes)

* sudo yum update -y

* Install:
  * Java  
     * sudo amazon-linux-extras install java-openjdk11 -y
  * Git 
    * sudo yum install git -y
  * Docker
    * sudo amazon-linux-extras install docker -y
    * sudo service docker start
    * sudo usermod -a -G docker ec2-user
    * sudo chmod 666 /var/run/docker.sock
    * exit and re-ssh
    * docker info
  * Docker compose
    * sudo chkconfig docker on
    * sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
    * sudo chmod +x /usr/local/bin/docker-compose
    * docker-compose version

* Pull git:
  * git clone https://github.com/okiyama/writing.game.git
  * Password is personal access token, username is okiyama

* Setup swap
  * sudo mount /dev/xvda2 /mnt
  * sudo dd if=/dev/zero of=/mnt/swapfile bs=1M count=8192
    * 8GB
    * Instance will lock up while copying
  * sudo chown root:root /mnt/swapfile
  * sudo chmod 600 /mnt/swapfile
  * sudo mkswap /mnt/swapfile
  * sudo swapon /mnt/swapfile
  * vim /etc/fstab add to bottom:
    * /mnt/swapfile swap swap defaults 0 0
  * sudo swapon -a

* Copy over resources folder
  *     scp -r -i C:\Users\Julian\.ssh\aws_ec2.pem C:\Users\Julian\IdeaProjects\writing.game\src\main\resources\ ec2-user@ec2-3-131-78-151.us-east-2.compute.amazonaws.com:/home/ec2-user/writing.game/src/main/
  
* Copy over secrets folder
  *     scp -r -i C:\Users\Julian\.ssh\aws_ec2.pem C:\Users\Julian\IdeaProjects\writing.game\secrets\ ec2-user@ec2-3-131-78-151.us-east-2.compute.amazonaws.com:/home/ec2-user/writing.game/

* Build docker image
  * cd /home/ec2-user/writing.game
  * chmod +x ./gradlew
  * ./gradlew jibDockerBuild
  
* Run app
  * cd docker
  * docker-compose up