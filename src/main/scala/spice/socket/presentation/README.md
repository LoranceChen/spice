### DeCode network bytes stream EnCode object.
1. data encode and decode
2. reactive driver after serialize/deserialize operation
3. custom data structure composed by ``

#### position on whole Protocol
Protocol's data structure decide by this layer, in other word, if this layer feeling uncomfortable, session and application's implement
should be adjust.

#### Decided
session use 1 byte (256 chance) to define itself needed protocol,such as client able change some pattern on below and this action not
affect applications' logic. and presentation also has it's 1 byte protocol.

datagram view as this:

Server      ---------------------------------------------------------------------|---------------------------------- Client
DataLayer  |<----- ^App  ----->|<---  ^pst ---->|<--- ^sen --->|<--..TCP/IP ..-->|      ....as Server

Structure : ProtoID: Int byte + Length: Int 4 Byte + Contains: DeCoding[_ <: EnCoding] Length Byte

#### 其他
应用层数据的编码 - 应用程序层只需要标记需要解码的数据即可,长度信息在表示层附加.
