package com.waterwagen

import groovy.sql.Sql

String url = 'jdbc:oracle:thin:waterwagen/superduper79nomelissa@localhost:1521:XE'
String username = null
String password = null
String driver_name = 'oracle.jdbc.OracleDriver' 
Sql db = Sql.newInstance(url, username, password, driver_name)
String query = 'select * from books'

def result = db.rows query
assert result[0][1] == 'Moby Dick'
assert result[0]['TITLE'] == 'Moby Dick'

// print whole row
println "Rows:"
int row_count = 0
db.eachRow(query) 
{
	println "Row ${++row_count}: ${it}"
}

// print only a certain column
println ''
println "Titles:"
db.eachRow(query) 
{
	println it.TITLE
}