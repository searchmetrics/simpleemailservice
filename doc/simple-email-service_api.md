# SimpleEmailService API Documentation

### `POST /sendEmail/`
This will send a new E-Mail to all addresses specified into the JSON object.
This needs a JSON `SendEmailRequest`.

### `SendEmailRequest`
This is a JSON object containing all information to send the E-Mail.
 * `toEmailList = Array`: An array of addresses the E-Mail should be sent to
 * `subject = String`: The mail's subject
 * `messageBody = String`: The main message of the E-Mail (can be HTML)
 * `attachmentList = Array`: An array of `Attachment`s (This is optional)

### `Attachment`
A simple attachment for an E-Mail
 * `name = String`: The file name of the attachment.
 * `mimeType = String`: The mime type of the attachment
 * `data = String`: A base64 encoded string of the file
