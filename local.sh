#!/usr/bin/env bash
./sbt compile stage
heroku local web
