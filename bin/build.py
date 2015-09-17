#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import sys
import subprocess
import shutil

gitBranch = 'sentinel2'
gitUrl = 'http://readonly:den10.malice@gitlab.mogujie.org/bigdata/jarvis2.git'

rootDir = os.path.abspath(os.path.join(os.path.dirname(__file__), os.path.pardir))
libDir = rootDir + '/lib'
confDir = rootDir + '/conf'
srcDir = rootDir + '/src'

def execute(workdir, cmd):
    exitCode = subprocess.call('cd %s && %s' % (workdir, cmd), shell=True)
    if exitCode != 0:
        exit(1)

def gitPull():
    if(os.path.exists(srcDir + '/.git')):
        execute(srcDir, 'git pull')
    else:
        execute(rootDir, 'git clone -b %s %s %s' % (gitBranch, gitUrl, srcDir))

def mvnInstall():
    execute(srcDir, 'mvn clean install')

def copyLibs():
    if(os.path.exists(libDir)):
        shutil.rmtree(libDir)

    execute(rootDir, 'mkdir -p %s && find ./src -name *.jar -exec cp {} ./lib/ \;' % libDir)
    execute(srcDir, 'mvn clean')

def main():
    gitPull()
    mvnInstall()
    copyLibs()
    print('BUILD SUCCESS')

if __name__ == '__main__':
    main()