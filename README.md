
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
|```/subscription/:accountType/:ref/:amlsRegistrationNumber/status``` | GET | Gets the current status of supervision |
|```/subscription/:accountType/:ref/:amlsRegistrationNumber``` | GET | Retrieves the currently held supervision details |
|```/payment/:accountType/:ref/:amlsRegistrationNumber``` | GET | Retrieves information on the last fee response for this supervision |
|```/payment/:accountType/:ref/:amlsRegistrationNumber``` | POST | Save a payment previously made through `pay-api` |
|```/payment/:accountType/:ref/payref/:paymentReference``` | GET | Retrieve the latest payment made given the payment reference |
|```/payment/:accountType/:ref/amlsref/:amlsRegistrationNumber``` | GET | Retrieve the latest payment made given the AMLS registration number |
|```/payment/:accountType/:ref/refreshstatus``` | PUT | Refreshes the status of a payment, given an amls reference in the body |
|```/payment/:accountType/:ref/:paymentReference/bacs``` | PUT | Updates the BACS status of a payment (see below) |
|```/payment/:accountType/:ref/:paymentReference/bacs``` | POST | Creates a new BACS payment (see below) |

## Retrieving a payment

Whether you retrieve a payment given the payment reference or an AMLS reference number, a payment will be returned with the following fields:

| Name | Description | Optional |
| ---- | ----- | ----- |
| _id | The payment id | |
| amlsRefNo | The AMLS registration number that created the payment | |
| reference | The payment reference | |
| amountInPence | The payment amount in pence ||
| status | The current payment status. One of `Created`, `Successful`, `Sent`, `Failed` or `Cancelled` |
| createdAt | The date/time the payment was created ||
| isBacs | Whether or not the payment is intended as a BACS payment | yes |
| updatedAt | The date/time this record was last modified | yes |

## Refreshing the status of a payment

To refresh the payment status, send a PUT request to `/payment/:accountType/:ref/refreshstatus` with the payment reference in the following JSON format:

```
{
    "paymentReference": "X000000000000123"
}
```

## Updating payment BACS status

To update the BACS status of a given payment, contact `/payment/:accountType/:ref/:paymentReference/bacs` as a PUT request with the following JSON payload:

```
{
  "isBacs": true|false
}
```

## Creating a new BACS payment

This endpoints creates a new payment in the database which is already configured to be BACS. This is a POST to `/payment/:accountType/:ref/:paymentReference/bacs` with the following JSON data:

```
{
    "amlsReference": "X000000000000123",
    "paymentReference": "X000000000000456",
    "safeId": "X000000000000456",
    "amountInPence": 10000
}
```
