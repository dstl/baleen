# Baleen

**Baleen 2 has now reached the end of it's life, and we encourage all users to move to [Baleen 3](https://github.com/dstl/baleen3).**
There is no intention to provide further updates or any significant support for Baleen 2.

[![Build Status](https://travis-ci.org/dstl/baleen.svg?branch=master)](https://travis-ci.org/dstl/baleen) [![Coverage Status](https://coveralls.io/repos/github/dstl/baleen/badge.svg?branch=master)](https://coveralls.io/github/dstl/baleen?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/27988357f1274bec85626a85ec093e97)](https://www.codacy.com/app/jbaker-dstl/baleen?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dstl/baleen&amp;utm_campaign=Badge_Grade)

Baleen is an extensible text processing capability that allows entity-related information to be extracted from unstructured and semi-structured data sources. It makes available in a structured format things of interest otherwise stored in formats such as text documents - references to people, organisations, unique identifiers, location information.

Baleen is written in [Java 8](http://www.oracle.com/java/javase/downloads/jre8-downloads-2133155.html) using the software project management tool [Maven 3](http://maven.apache.org) and draws heavily on the [Apache Unstructured Information Management Architecture (UIMA)](http://uima.apache.org) which provides a framework, components and infrastructure to handle unstructured information management.    

Baleen was written by the Defence Science and Technology Laboratory (Dstl) in support of UK Defence users looking to extract entities and search unstructured text documents. License information can be found in the accompanying `LICENSE.txt` file in this repository and the licenses of libraries on which Baleen is dependent are listed in the file `THIRD-PARTY.txt`.

Baleen is still under active development, and is released here not as a final product but as a work in progress. As such, there may be bugs, issues, typos, mistakes in the documentation, and more. We hope that contributions from other users will improve Baleen and result in a better framework for others to use.

## Upgrading to 2.4 and later

Baleen 2.4 (and later) contain a number of changes that may make it incompatible with older pipelines - for upgrade guidance see the [Upgrading Between Versions](https://github.com/dstl/baleen/wiki/Upgrading-Between-Versions#upgrading-230-to-240) wiki page.

### Getting Started

Baleen includes an in-built server, which hosts full documentation and guides on how to use Baleen.
To get started, you will need to launch this server and read this documentation.
To launch the server, run the following command.

> java -jar baleen-2.8.0-SNAPSHOT.jar

Once running, the server can be accessed at [http://localhost:6413](http://localhost:6413).

If you require the Javadoc to be available through the in-built server, then you should place the Baleen Javadoc JAR in the same directory as the Baleen JAR.

## Prerequisites

### Running

To run Baleen, you will need:

*   A sensible amount of RAM. Start with 4GB and alter according to the number of annotators being employed. 
*   Java 8 or larer
 
### Developing

The develop with Baleen, we suggest you use:

*   Oracle Java JDK 1.8
*   Eclipse Mars or greater (assumed to include Maven)
*   Maven

Baleen requires Java 8 or later.

## Licence

Crown copyright 2017-2019

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this software except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License

## Data

Baleen contains data derived from other data sources. For more information, please refer to the Baleen source code.

### Code-Point Open

Licensed under the Open Government Licence (OGL) v3 - [http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/](http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/)

Contains OS data (c) Crown copyright and database right 2015

Contains Royal Mail data (c) Royal Mail copyright and database right 2015

Contains National Statistics data (c) Crown copyright and database right 2015

### Countries JSON

Licensed under the ODC Open Database Licence (ODbL) 1.0 - [http://opendatacommons.org/licenses/odbl/1.0/](http://opendatacommons.org/licenses/odbl/1.0/)

Any rights in individual contents of the database are licensed under the Database Contents License - [http://opendatacommons.org/licenses/dbcl/1.0/](http://opendatacommons.org/licenses/dbcl/1.0/)

### Countries GeoJSON

Licensed under the ODC Public Domain Dedication and Licence (PDDL) 1.0 - [http://opendatacommons.org/licenses/pddl/1.0/](http://opendatacommons.org/licenses/pddl/1.0/)

### OpenNLP Language Models

Licensed under the Apache Software License 2.0 - [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)