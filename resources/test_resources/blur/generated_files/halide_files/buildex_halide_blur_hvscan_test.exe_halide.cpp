#include <Halide.h>
  #include <vector>
  using namespace std;
  using namespace Halide;
  int main(){ 

Var x_0;
Var x_1;
Var x_2;
Var x_3;
ImageParam input_1(UInt(8),2);
Func output_1;
Expr output_1_p__0 =  (  ( ((43691  * cast<uint32_t>( (  ( (((43691  * cast<uint32_t>( (  ( (cast<uint32_t>(input_1(x_0,x_1) ) + cast<uint32_t>(input_1(x_0+1,x_1) ) + cast<uint32_t>(input_1(x_0+2,x_1) )) ) & 65535 ) )) >> cast<uint32_t>(17 )) + ((43691  * cast<uint32_t>( (  ( (cast<uint32_t>(input_1(x_0,x_1+1) ) + cast<uint32_t>(input_1(x_0+1,x_1+1) ) + cast<uint32_t>(input_1(x_0+2,x_1+1) )) ) & 65535 ) )) >> cast<uint32_t>(17 )) + ((43691  * cast<uint32_t>( (  ( (cast<uint32_t>(input_1(x_0,x_1+2) ) + cast<uint32_t>(input_1(x_0+1,x_1+2) ) + cast<uint32_t>(input_1(x_0+2,x_1+2) )) ) & 65535 ) )) >> cast<uint32_t>(17 ))) ) & 65535 ) )) >> cast<uint32_t>(17 )) ) & 255 ) ;
output_1(x_0,x_1) = cast<uint8_t>( clamp(output_1_p__0,0,255) );

vector<Argument> arguments;
arguments.push_back(input_1);;
output_1.compile_to_file("halide_out_0",arguments);
return 0;
}
