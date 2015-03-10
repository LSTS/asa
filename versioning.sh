#/bin/bash

#echo "Auto Version: `pwd`"

CODE=`git tag | grep -c ^v[0-9]`
NAME=`git describe --dirty | sed -e 's/^v//'`
COMMITS=`echo ${NAME} | sed -e 's/[0-9\.]*//'`

DATE=`date +"%d-%m-%Y"`


if [ "x${COMMITS}x" = "xx" ] ; then
    VERSION="${NAME}"
else
    BRANCH=" (`git branch | grep "^\*" | sed -e 's/^..//'`)"
    VERSION="${NAME}${BRANCH}"
    VERSION=`echo "${VERSION}" | tr '\/' '-'`
fi

#echo "   Code: ${CODE}"
#echo "   Ver:  ${VERSION}"

cat ./build.gradle | \
    sed -e "s/versionCode [0-9][0-9]*/versionCode ${CODE}/" \
        -e "s/versionName \".*\"/versionName \"${VERSION}\"/" \
    > ./build.gradle.backup

cp ./build.gradle.backup ./build.gradle


exit 0
