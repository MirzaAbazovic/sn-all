status_url=http://localhost:$2/$1/status

echo "Checking simulator status ..."
echo $status_url

if \
    wget -q -O - $status_url | grep -vi 'failure' >/dev/null \
    && wget -q -O - $status_url | grep -i 'Overall result:.*OK' >/dev/null \
    && wget -q -O - $status_url | grep -i 'Server started:.*'$(date -I) >/dev/null
then
    echo "Ok: Application is running."
    exit 0
else
    echo "Application status has failures - please check"
    wget -q -O - $status_url
    exit 1
fi