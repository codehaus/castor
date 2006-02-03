#!/bin/sh

# $Id$

if [ -z "$JAVA_HOME" ] ; then
  JAVA=`which java`
  if [ -z "$JAVA" ] ; then
    echo "Cannot find JAVA. Please set your PATH."
    exit 1
  fi
  JAVA_BIN=`dirname $JAVA`
  JAVA_HOME=$JAVA_BIN/..
fi

JAVA=$JAVA_HOME/bin/java

CLASSPATH=$CLASSPATH:../build/classes:../build/tests
CLASSPATH=`echo ../lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo ../lib/tests/*.jar | tr ' ' ':'`:$CLASSPATH

$JAVA -cp $CLASSPATH Main -execute $1 $2 $3 $4 $5 $6



