#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import re
import sys
import subprocess

rootDir = os.path.abspath(os.path.join(os.path.dirname(__file__), os.path.pardir))
libDir = rootDir + '/lib'
confDir = rootDir + '/conf'
logsDir = rootDir + '/logs'

javaHome = os.environ.get('JAVA_HOME')
javaOps = '-Xms8G -Xmx8G -Xmn512M -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -Dsentinel.log.dir=%s' % logsDir

def execute(cmd):
    subprocess.call(cmd, shell=True)

def start(mainClass, role):
    execute('nohup %s/bin/java %s -Dsentinel.role=%s -cp %s:%s/* %s > %s/app.log 2>&1 &' % (javaHome, javaOps, role, confDir, libDir, mainClass, logsDir))

def stop(mainClass):
    execute('%s/bin/jps -lm | grep %s | awk \'{print $1}\' | xargs kill' % (javaHome, mainClass))

def restart(mainClass, role):
    stop(mainClass)
    start(mainClass, role)

def printHelp():
    print('Usage: python jarvis-daemon.py start|stop|restart server|logserver|client|restful\n')

def checkJavaVersion():
    proc = subprocess.Popen("java -version", shell=True, stderr=subprocess.PIPE)
    match = re.search(r'java version "(\d+\.\d+)', proc.stderr.readline())
    if match:
        javaVersion = float(match.group(1))
        return javaVersion >= 1.7
    return False

def main():
    commandList = ['start', 'stop', 'restart']

    mainClassDcit = {
        'server': 'com.mogujie.jarvis.server.JarvisServer',
        'logserver': 'com.mogujie.jarvis.logserver.JarvisLogServer',
        'worker': 'com.mogujie.jarvis.worker.JarvisWorker',
        'rest': 'com.mogujie.jarvis.rest.JarvisRestServer'
    }

    if len(sys.argv) != 3:
        printHelp()
        exit(1)
    else:
        command = sys.argv[1]
        role = sys.argv[2]

        if command in commandList and role in mainClassDcit.keys():
            mainClass = mainClassDcit[role]
            if command == 'start':
                if role == 'worker' and not checkJavaVersion():
                    print('Error: Java version should >= 1.8 to support Presto job.')
                    exit(1)

                print('Starting jarvis %s......' % role)
                start(mainClass, role)
            elif command == 'stop':
                print('Stoping jarvis %s......' % role)
                stop(mainClass)
            elif command == 'restart':
                print('Restarting jarvis %s......' % role)
                restart(mainClass, role)
        else:
            printHelp()
            exit(1)


if __name__ == '__main__':
    main()