#!/bin/sh

HADOOP_CMD="/hdfs/hadoop-1.0.4/bin/hadoop"
DATASET="test8m-svm"
DATASET_HDFSPATH="/user/root/dataset"

HDFS_NAMENODE=$1
SPARK_PATH="/root/spark"
SPARK_MASTER=$2

# upload dataset

$HADOOP_CMD dfs -mkdir $DATASET_HDFSPATH
$HADOOP_CMD dfs -put $DATASET $DATASET_HDFSPATH

# submit spark application

"$SPARK_PATH/bin/spark-submit" --class org.viirya.spark.KMeansSVMFormat --master $SPARK_MASTER --deploy-mode client KMeansWithSVMF
ormat-assembly-1.0.jar -k 350 --numIterations 10 --numDimensions 784 "$HDFS_NAMENODE$DATASET_HDFSPATH/$DATASET"

