$(function(){
	$("#sendBtn").click(test);
});
function test() {
//                $.get("/community/alpha/testGet",function(data,status){
//                    alert("数据: " + data + "\n状态: " + status);
//                });
                var Bucket = 'community-header-1252397182';
                var Region = 'ap-nanjing';     /* 存储桶所在地域，必须字段 */

                // 初始化实例
                var cos = new COS({
                    getAuthorization: function (options, callback) {
                        // 异步获取临时密钥
                        $.get('/community/alpha/testGet', {
                        }, function (data) {
                                        alert("数据: " + data );
                            var credentials = data && data.credentials;
                            if (!data || !credentials) return console.error('credentials invalid');
                            callback({
                                TmpSecretId: credentials.tmpSecretId,
                                TmpSecretKey: credentials.tmpSecretKey,
                                XCosSecurityToken: credentials.sessionToken,
                                // 建议返回服务器时间作为签名的开始时间，避免用户浏览器本地时间偏差过大导致签名错误
                                StartTime: data.startTime, // 时间戳，单位秒，如：1580000000
                                ExpiredTime: data.expiredTime, // 时间戳，单位秒，如：1580000900
                            });
                        });
                    }
                });
}
