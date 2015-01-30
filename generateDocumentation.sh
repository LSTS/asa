#/bin/bash

rm -r doxygenOutput/
mkdir doxygenOutput

doxygen asa.doxyfile

echo "------------------------------------------------------------------------"
echo ""
echo "main html file in doxygenOutput/html/index.html"
echo ""

