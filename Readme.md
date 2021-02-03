# Ropsten Faucet

This Projects is an implementation of a simple Test-Ether faucet, which is rate limited using Redis and implemented with Angular and Ktor.

[![Build Status](https://drone.rpanic.com/api/badges/rpanic/ropsten-faucet/status.svg)](https://drone.rpanic.com/rpanic/ropsten-faucet)

You can use the [live-version here](https://faucet.rpanic.com/)

## Structure

The Projects consists of the Angular-Frontend and the Kotlin based backend.

## Usage

Before startup, make sure you create a docker volume for the public and private key of the distributing account using `docker volume create faucetkeys`
Put a file named `keys.txt` into the volume containing the publickey on the first and the private key on the second line.

Make sure to set up a Redis-Server which is reachable using a docker network. Default is `redis://redis:6379`, but can be changed in application.conf.

## Startup

Then build the image with `docker build -t faucet .`

Finally, start it up with `docker run -d --name faucet -p 8080:8080 --network faucetnetwork -v faucetkeys:/app/config faucet`
