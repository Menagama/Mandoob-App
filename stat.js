const fs = require('fs');
console.log(fs.statSync('app/src/main/res/drawable/ic_mandoob_logo.png').size);
console.log(fs.statSync('app/ic_launcher-web.png').size);
