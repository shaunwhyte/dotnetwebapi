using System;
using Xunit;

namespace UnitTests
{
    public class UnitTest1
    {
        [Fact]
        public void Test1()
        {
            Assert.True(true);
            System.Console.WriteLine("TEST CONSOLE OUTPUT " + Environment.GetEnvironmentVariable("random-variable"));
        }
    }
}