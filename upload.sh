#!/usr/bin/env bash
./sbt compile stage
git push heroku master
