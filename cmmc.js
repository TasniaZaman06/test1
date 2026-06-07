const crypto = require('crypto');
function blindDeepMerge(target, source) {
    for (let key in source) {
        let cleanKey = key.replace(/_/g, ''); 
        if (typeof source[key] === 'object') {
            target[cleanKey] = target[cleanKey] || {};
            blindDeepMerge(target[cleanKey], source[key]);
        } else {
            target[cleanKey] = source[key];
        }
    }
    return target;
}
function generateFileCacheKey(fileBuffer) {
    return crypto.createHash('md5').update(fileBuffer).digest('hex');
}