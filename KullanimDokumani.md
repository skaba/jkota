# Kullanım Dökümanı #

## Giriş ##
JKota, Türk Telekom ADSL ve Kablonet aboneleri için kota izleme programıdır. Kurulumdan sonra sistem tepsisine yerleşir ve internet kullanımına bağlı olarak kota miktarını gözlemler. Belirli periyotlarla güncelleme yaptığı için kota takibi rahat olur, ayrıca kullanıcıları servis sağlayıcıların sitesine gitme zahmetinden kurtarır. Java ile yazıldığından birden fazla platform üstünde çalışma imkânı vardır.

## Yapılandırma ##

### JKota'nın çalışması için gerekenler ###
JKota Java ile yazıldığından çalışması için sistemde Java Runtime Enviroment (En az 1.6) kurulu olması gerekmektedir.

Kota siteleri üye giriş kontrolü için Captcha sistemini kullandığından JKota bu aşamayı geçmek için captchakiller'ı kullanır. Bu nedenle programın kurulumuna geçmeden önce http://www.captchakiller.com adresinden kullanıcıların bir üyelik almaları gereklidir. Üyelik gerçekleştikten sonra size tahsis edilen API anahtarı program işleyişinde kullanılacaktır.

### Yapılandırma ###
Linux kullanıcıları arşivi açtıktan sonra terminalden

sh jkota komutu ile,

Windows kullanıcı ise arşivi açtıktan sonra jkota.exe isimli programı kullanarak yapılandırmayı başlatabilirler.

Program yapılandırmasında ilk etapta bir Ana Şifre sorulur. Bu Ana Şifre programın her açılışında kullanılacağı için dikkatli bir şekilde seçilmelidir. Daha sonra çıkan ekranda sırasıyla şunlar girilmelidir:

  * Kullanıcı adı: ADSL hesabınızda kullandığınız kayıtlı isminiz ya da Kablonet müşteri numaranız
  * Şifre: Hesabınıza ait şifre
  * API Key: Kuruluma geçmeden önce http://www.captchakiller.com adresinden edineceğiniz API anahtarıdır. Siteye üyelik için işlemleri tamamladıktan sonra e-postanıza onay mesajı gelecektir. Bu aşamayı geçip siteye giriş yaptıkan sonra sol taraftaki menüden API Docs bağlantısını kullanarak size ait API anahtarını görebilirsiniz.
  * Güncelleme Sıklığı : Kota bilgilerinin yapılacağı güncelleme sıklığını burada belirleyebilirsiniz.

## Kullanım ##

Programın kullanımı son derece basittir. Yapılandırmadan sonra fazladan bir şeye gerek kalmadan JKota sistem tepsisine yerleşir ve sizin belirlediğiniz süre ile güncelleme yapar. Fare imlecini JKota simgesine götürdüğünüzde o anki güncel kota bilgilerinizi görebilirsiniz. Yine JKota simgesini kullanarak önceki ayarları değiştirebileceğiniz gibi programdan da çıkış yapabilirsiniz.