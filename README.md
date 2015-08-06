# ahnentafel

A Clojure web app to display genealogical data from a GEDCOM file.

The intended usage is to allow a URL to be sent to relatives to allow
them to review genealogical data and to email back changes. Thus it is
only for read-only usage.

## Configuration

Environment Variables:

* `PORT`
* `GEDCOM-FILE` - URI to file. "resource" and "s3" schemes handled specially.
* `ANALYTICS-ID`
* `START-RECORD`
* `AWS_ACCESS_KEY_ID` - needed when GEDCOM-FILE scheme is "s3"
* `AWS_SECRET_KEY` - needed when GEDCOM-FILE scheme is "s3"

## License

Copyright © 2015 Eclipse Public License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
