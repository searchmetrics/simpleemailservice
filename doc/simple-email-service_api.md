# SimpleEmailService API Documentation

### `POST /sendEmail/`
This will send a new E-Mail to all addresses specified into the JSON object.
This needs a JSON `SendEmailRequest`.

#### `SendEmailRequest`
This is a JSON object containing all information to send the E-Mail.
 * `toEmailList = Array`: An array of addresses the E-Mail should be sent to
 * `subject = String`: The mail's subject
 * `messageBody = String`: The main message of the E-Mail (can be HTML)
 * `attachmentList = Array`: An array of `Attachment`s (This is optional)

#### `Attachment`
A simple attachment for an E-Mail
 * `name = String`: The file name of the attachment.
 * `mimeType = String`: The mime type of the attachment
 * `data = String`: A base64 encoded string of the file


### `GET /sendStatistics/`
This will get and return the send statistics from AWS of the last two weeks.
It will return a `SendStatistics` JSON object.

#### `SendStatistics`
 * `dataPoints = Array`: An Array of `DataPoint`s

#### `DataPoint`
A `DataPoint` is a JSON object storing the send statistics of the last 15 min.
after the `timestamp`.
 * `timestamp = Number`: Java Date (passed milliseconds since January 1, 1970 00:00:00.000 GMT)
 * `deliveryAttempts = Number`: Number of delivery attempts
 * `bounces = Number`: Number of bounces
 * `complaints = Number`: Number of complaints
 * `rejects = Number`: Number of rejects
