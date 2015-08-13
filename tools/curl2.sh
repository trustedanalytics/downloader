#!/bin/bash
#
# Copyright (c) 2015 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


set -e

DAS_URL=$1
URL=$2
TOKEN=`cf oauth-token|grep bearer`

CURL_OPTS="-s -S -H \"Content-Type: application/json\" -H \"Authorization: $TOKEN\"  $DAS_URL"
PUT="curl -X POST $CURL_OPTS/rest/downloader/requests -d '{\"source\":\"$URL\"}'}"
request_id=`eval $PUT`
echo "Request ID: " $request_id


