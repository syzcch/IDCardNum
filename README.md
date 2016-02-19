# IDCardNum
Create random ID Card Number

很多网站或者一些其他应用在注册的时候需要输入用户的身份证信息，随便输入自己的身份信息是非常危险的。
一个人的身份证是有他出生的省市区、生日、性别、随机数字以及最后一位校验码组成。
通过省市区、性别、生日等信息可以生成一个身份证号用于注册，可以避免输入自己的身份证信息，保护自己的隐私。

用户需要输入省市区、生日、性别等信息，自动生成身份证号，并保存到剪贴板中，用户可以方便的使用该号码。

可以批量生成1-20个选定省市区和生日性别的身份证号。

省市区的数据以及相应的编码来自网络，抓下来数据进行清理整理并建Sqlite库。

目前的缺陷：多语言适配没有做，多屏幕适配没有做，界面较简陋，待完善。

minSdkVersion 15

This app used for generating a random ID Card number when user need input ID card information to some websites or APPs.

