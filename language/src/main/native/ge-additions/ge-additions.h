#ifndef GE_ADDITIONS_H
#define GE_ADDITIONS_H

#include "ed25519/ge.h"

void ge_scalarmult(ge_p3* h, const unsigned char* a, const ge_p3* A);

#endif
