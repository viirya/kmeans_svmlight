## kmeans_svmlight

### build

    sbt/sbt assembly

### Dataset

Get [the infinite MNIST dataset](http://leon.bottou.org/projects/infimnist). Generate the dataset:

    make
    ./infimnist svm 0 8109999 > mnist8m-libsvm.txt

Upload the dataset to HDFS:

    hadoop dfs -put mnist8m-libsvm.txt dataset/.

### Run test

    bin/spark-submit --class org.viirya.spark.KMeansSVMFormat --master [spark master url] --deploy-mode client KMeansWithSVMFormat-assembly-1.0.jar -k 1000 --numIterations 100 --numDimensions 784 hdfs://hostname.to.hdfs:54310/path/to/dataset/mnist8m-libsvm.txt 

