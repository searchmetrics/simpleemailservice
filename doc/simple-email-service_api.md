# SimpleEmailService API Documentation

### `POST /uploadAttachment/`
This will upload an attachment to S3 and return a download link to it. It
requires an [Attachment](#attachment) and returns a
[UploadAttachmentResponse](#uploadattachmentresponse).

### `POST /sendEmail/`
This will send a new E-Mail to all addresses specified into the JSON object.
This needs a JSON [SendEmailRequest](#sendemailrequest).

### `GET /sendStatistics/`
This will get and return the send statistics from AWS of the last two weeks.
It will return a [SendStatistics](#sendstatistics) JSON object.

### SendStatistics
 * `dataPoints = Array`: An Array of [DataPoints](#datapoint)

### DataPoint
A `DataPoint` is a JSON object storing the send statistics of the last 15 min.
after the `timestamp`.
 * `timestamp = Number`: Java Date (passed milliseconds since January 1, 1970 00:00:00.000 GMT)
 * `deliveryAttempts = Number`: Number of delivery attempts
 * `bounces = Number`: Number of bounces
 * `complaints = Number`: Number of complaints
 * `rejects = Number`: Number of rejects

### UploadAttachmentResponse
Will be returned from `POST /uploadAttachment/`.
 * `statusMessage`: Message containing errors.
 * `url`: Download URL of the attachment on S3

### SendEmailRequest
This is a JSON object containing all information to send the E-Mail.
 * `toEmailList = Array`: An array of addresses the E-Mail should be sent to
 * `subject = String`: The mail's subject
 * `messageBody = String`: The main message of the E-Mail (can be HTML)
 * `attachmentList = Array`: An array of [Attachments](#attachment) (This is optional)

### Attachment
A simple attachment for an E-Mail
 * `name = String`: The file name of the attachment.
 * `mimeType = String`: The mime type of the attachment
 * `data = String`: A base64 encoded string of the file

## Examples

### Upload an attachment

```bash
$ curl -H "Content-Type: application/json" -X POST -d '{"name":"anotherName.txt","mimeType":"text/plain","data":"VGhpcyBpcyBhIHRlc3Q="}' http://localhost:10001/uploadAttachment

{"statusMessage":"Uploaded attachment.","url":"https://example.s3.eu-central-1.amazonaws.com/a_very_long_url"}
```

### Send an E-Mail

```bash
$ curl -H "Content-Type: application/json" -X POST -d '{"toEmailList":["user@domain.tld","another@another.tld"],"subject":"SimpleEmailService","messageBody":"Here is a message."}' http://localhost:10001/sendEmail

{"statusMessage":"E-Mail was sent."}
```
