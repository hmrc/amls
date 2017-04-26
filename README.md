amls
=============

Anti-money Laundering Supervision

[![Build Status](https://travis-ci.org/hmrc/amls.svg?branch=master)](https://travis-ci.org/hmrc/amls) [ ![Download](https://api.bintray.com/packages/hmrc/releases/amls/images/download.svg) ](https://bintray.com/hmrc/releases/amls/_latestVersion)

API
----

| PATH | Supported Methods | Description |
|------|-------------------|-------------|
|```/subscription/:accountType/:ref/:safeId``` | POST | Submits an application for supervision |
|```/subscription/:accountType/:ref/:amlsRegistrationNumber/update``` | POST | Submits an updated application for supervision |
|```/subscription/:accountType/:ref/:amlsRegistrationNumber/variation``` | POST | Submits a variation to current supervision details |
|```/subscription/:accountType/:ref/:amlsRegistrationNumber/renewal``` | POST | Submits an application to renew supervision |
|```/subscription/:accountType/:ref/:amlsRegistrationNumber/status``` | GET | Gets the current status of supervision
|```/subscription/:accountType/:ref/:amlsRegistrationNumber``` | GET | Retrieves the currently held supervision details
|```/payment/:accountType/:ref/:amlsRegistrationNumber``` | GET | Retrieves information on the last fee response for this supervision

