#!/bin/sh

sleep 10

echo "Creating cluster..."
redis-cli --cluster create \
  redis_1:7000 \
  redis_2:7001 \
  redis_3:7002 \
  redis_4:7003 \
  redis_5:7004 \
  redis_6:7005 \
  --cluster-replicas 1 \
  --cluster-yes

if [ $? -eq 0 ]; then
  echo "Cluster is ready"
else
  echo "Error"
  exit 1
fi

tail -f /dev/null
