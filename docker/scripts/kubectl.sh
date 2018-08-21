#!/usr/bin/env sh

if [ -n "$KUBECTL_CONFIG_PATH" ]
then
    rkubectl --kubeconfig $KUBECTL_CONFIG_PATH $@
else
    exit 1
fi