 apk add openssh
 rc-update add sshd
/etc/init.d/sshd start

addgroup -S hunting && adduser -S hunter -G hunting