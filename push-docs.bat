call lein doc
cd doc
call git add -A
call git commit -m"Documentation update"
call git push origin gh-pages
cd..
