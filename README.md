# Searchmetrics Simple Email Service

![header](header.png)

This RESTful service can send E-Mails with attachments using an AWS API. If you
want you can also first upload your attachments to a S3 bucket and only add a
link to it in your E-Mail.

You can find the documentation and some examples how to use it
[here](doc/simple-email-service_api.md).

## How to run this service

* First install [Maven](https://maven.apache.org/) and [IntelliJ](https://www.jetbrains.com/idea/).

* Check out this repository:
```bash
git clone https://github.com/searchmetrics/simpleemailservice/
# OR:
git clone git@github.com:searchmetrics/simpleemailservice
```

* Open the project in IntelliJ

* Start the main function in
  `src/main/java/com/searchmetrics/simpleEmailService/SimpleEmailApplication.java`
  (it won't work the first time).

* Change the build configuration:
  Program arguments: `server application.yaml`

* To send E-Mails over AWS, you have to save your access key id and secret access
  key into `~/.aws/credentials`.

* If you've set up everything, you can now start the service in IntelliJ and use
  it as described in the [documentation](doc/simple-email-service_api.md).

## Bug reports

You can report bugs on [GitHub](https://github.com/searchmetrics/simpleemailservice/issues/new).
The same goes for suggestions, but you can't expect them to be realized.
