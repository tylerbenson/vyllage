#!/bin/bash -ex

LAUNCH_CONFIG_NAME="vyllage-launch-config-NO-NR"
AUTOSCALING_GROUP_NAME="vyllage-auto-scaling-group"

aws autoscaling delete-auto-scaling-group --auto-scaling-group-name $AUTOSCALING_GROUP_NAME | true
aws autoscaling delete-launch-configuration --launch-configuration-name $LAUNCH_CONFIG_NAME | true

# Create Launch Config
aws autoscaling create-launch-configuration --launch-configuration-name $LAUNCH_CONFIG_NAME \
	--image-id ami-5189a661 --instance-type t2.micro \
	--key-name ec2-tyler \
	--iam-instance-profile "VyllageInstanceRole" \
	--user-data file://instance-setup.sh \
	--instance-monitoring Enabled=false \
	--block-device-mappings "DeviceName=/dev/sda1,Ebs={VolumeType=gp2,VolumeSize=8,DeleteOnTermination=true}" \
	--security-groups sg-24646541 sg-f48b8f91 sg-cdcdcaa8 sg-60565705

# Create ASG
aws autoscaling create-auto-scaling-group \
	--auto-scaling-group-name $AUTOSCALING_GROUP_NAME \
	--launch-configuration-name $LAUNCH_CONFIG_NAME \
	--availability-zones us-west-2a \
	--min-size 0 --desired-capacity 0 --max-size 1 \
	--load-balancer-names "vyllage" --health-check-type ELB --health-check-grace-period 120

# Update CodeDeploy
aws deploy update-deployment-group \
	--application-name vyllage --current-deployment-group-name prod \
	--auto-scaling-groups $AUTOSCALING_GROUP_NAME \
	--ec2-tag-filters Key=env,Type=KEY_AND_VALUE,Value=prod

aws autoscaling describe-lifecycle-hooks --auto-scaling-group-name vyllage-auto-scaling-group
