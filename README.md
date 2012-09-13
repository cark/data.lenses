# cark.data.lenses

## Installing

The library being still in its infancy there is no clojar upload yet. So for now you need to :

### Clone the repository
    git clone git://github.com/cark/data.lenses.git
### Install cark/data.lenses in your local maven repository
    lein install

From there it's only a matter of requiring the library in your project.clj
    (defproject lenses.example "1.0.0-SNAPSHOT"
       :description "an example project for cark/data.lenses"
       :dependencies [[org.clojure/clojure "1.4.0"]
                      [cark/data.lenses "0.0.1-SNAPSHOT"]])

## Usage

API doc : http://cark.github.com/data.lenses/
Sample project : https://github.com/cark/data.lenses/tree/master/example
Tutorial/example : https://github.com/cark/data.lenses/blob/master/example/src/example/core.clj

## License

Copyright (c) Sacha De Vos and contributors. All rights reserved.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file epl.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.
You must not remove this notice, or any other, from this software.

