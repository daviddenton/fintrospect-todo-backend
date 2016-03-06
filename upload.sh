#!/usr/bin/env bash
set -e
./sbt compile stage
git push heroku master
