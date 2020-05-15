# har-collector-extension-selenium

Junit 5 extension for Selenium Webdriver and Allure that captures HTTP activity of browser and stores it into [HAR](https://en.wikipedia.org/wiki/HAR_(file_format)) file attached to Allure report. Analazying HAR files may be very useful for finding bugs and it's root cause especially when problem can't be easily reproduced or happens from time to time.

## How does it look?

When properly set, extension starts proxy server which browser working through therefore all incoming and outcoming HTTP requests are capturing by the extension, after the test execution HAR file with HTTP requests captured attaches to Allure report:
![](https://github.com/kotvertolet/har-collector-junit5-extension/blob/master/screenshots/har_allure_report.jpg)

HAR file can be viewed with various services like [this](http://www.softwareishard.com/har/viewer/):
![](https://github.com/kotvertolet/har-collector-junit5-extension/blob/master/screenshots/har_viewer_screenshot.jpg)

## How to use
