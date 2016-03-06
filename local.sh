#!/usr/bin/env bash
set -e
./sbt compile stage
heroku local web
